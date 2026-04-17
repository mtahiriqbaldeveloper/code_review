#!/usr/bin/env bash
set -euo pipefail

DEFAULT_BASE_URL="http://localhost:8080/api/coupons"
BASE_URL="$DEFAULT_BASE_URL"
TMP_BODY="$(mktemp)"
PASS_COUNT=0
FAIL_COUNT=0

usage() {
  cat <<'EOF'
Usage:
  ./test-coupons-api.sh [BASE_URL]
  ./test-coupons-api.sh --base-url BASE_URL
  ./test-coupons-api.sh -u BASE_URL

Examples:
  ./test-coupons-api.sh
  ./test-coupons-api.sh "localhost:8080"
  ./test-coupons-api.sh --base-url "localhost:8080"
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -u|--base-url)
      if [[ $# -lt 2 ]]; then
        echo "Error: missing value for $1"
        usage
        exit 2
      fi
      BASE_URL="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      BASE_URL="$1"
      shift
      ;;
  esac
done

normalize_base_url() {
  local input="$1"
  local normalized="$input"

  if [[ "$normalized" != http://* && "$normalized" != https://* ]]; then
    normalized="http://$normalized"
  fi

  if [[ "$normalized" != */api/coupons ]]; then
    normalized="${normalized%/}/api/coupons"
  fi

  echo "$normalized"
}

BASE_URL="$(normalize_base_url "$BASE_URL")"

cleanup() {
  rm -f "$TMP_BODY"
}
trap cleanup EXIT

run_test() {
  local name="$1"
  local expected_status="$2"
  local method="$3"
  local url="$4"
  local data="${5:-}"

  local status
  if [[ -n "$data" ]]; then
    status="$(curl -sS -o "$TMP_BODY" -w "%{http_code}" -X "$method" "$url" \
      -H "Content-Type: application/json" \
      -d "$data")"
  else
    status="$(curl -sS -o "$TMP_BODY" -w "%{http_code}" -X "$method" "$url")"
  fi

  if [[ "$status" == "$expected_status" ]]; then
    printf "[PASS] %s -> expected %s, got %s\n" "$name" "$expected_status" "$status"
    PASS_COUNT=$((PASS_COUNT + 1))
  else
    printf "[FAIL] %s -> expected %s, got %s\n" "$name" "$expected_status" "$status"
    printf "Response body:\n"
    cat "$TMP_BODY"
    printf "\n"
    FAIL_COUNT=$((FAIL_COUNT + 1))
  fi
}

echo "Running coupon API curl tests against: $BASE_URL"
echo

run_test \
  "Get coupons overview" \
  "200" \
  "GET" \
  "$BASE_URL"

run_test \
  "Create coupon (valid)" \
  "201" \
  "POST" \
  "$BASE_URL" \
  '{"code":"CODE_12_20","discount":12.00,"minBasketValue":20.00,"description":"12 for 20"}'

run_test \
  "Create coupon (duplicate code)" \
  "409" \
  "POST" \
  "$BASE_URL" \
  '{"code":"CODE_12_20","discount":12.00,"minBasketValue":20.00,"description":"duplicate should fail"}'

run_test \
  "Create coupon (invalid payload negative discount)" \
  "400" \
  "POST" \
  "$BASE_URL" \
  '{"code":"BAD_NEG","discount":-12.00,"minBasketValue":20.00,"description":"invalid"}'

run_test \
  "Create coupon (business invalid zero discount)" \
  "422" \
  "POST" \
  "$BASE_URL" \
  '{"code":"ZERO_DISC","discount":0.00,"minBasketValue":20.00,"description":"zero discount should fail business rule"}'

run_test \
  "Apply coupon (valid)" \
  "200" \
  "POST" \
  "$BASE_URL/applications" \
  '{"basket":{"value":60.00},"couponCode":"TEST_05_50"}'

run_test \
  "Apply coupon (not found)" \
  "404" \
  "POST" \
  "$BASE_URL/applications" \
  '{"basket":{"value":60.00},"couponCode":"<NOT-EXISTING-CODE>"}'

run_test \
  "Apply coupon (basket too low)" \
  "422" \
  "POST" \
  "$BASE_URL/applications" \
  '{"basket":{"value":1.00},"couponCode":"TEST_05_50"}'

run_test \
  "Get coupon applications (valid code)" \
  "200" \
  "GET" \
  "$BASE_URL/TEST_05_50/applications"

run_test \
  "Get coupon applications (invalid code)" \
  "404" \
  "GET" \
  "$BASE_URL/NO_SUCH_CODE/applications"

echo
echo "Test run complete: PASS=$PASS_COUNT FAIL=$FAIL_COUNT"

if [[ "$FAIL_COUNT" -gt 0 ]]; then
  exit 1
fi
