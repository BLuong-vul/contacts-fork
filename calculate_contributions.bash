#! /bin/bash

calculate_contributions() {
  local user="$1"
  local aliases="${user_aliases[$user]}"

  # If aliases exist, create an OR condition for git log
  if [[ -n "$aliases" ]]; then
    git log --shortstat --author="$user" --author="$aliases" |
      grep "files\? changed" |
      awk '{files+=$1; inserted+=$4; deleted+=$6} END \
             {print "files changed:", files, "lines inserted:", inserted, "lines deleted:", deleted}'
  else
    git log --shortstat --author="$user" |
      grep "files\? changed" |
      awk '{files+=$1; inserted+=$4; deleted+=$6} END \
             {print "files changed:", files, "lines inserted:", inserted, "lines deleted:", deleted}'
  fi
}

print_contributions_for() {
  local contributors=("$@")
  for contributor in "${contributors[@]}"; do
    echo "Contributions for $contributor:"
    calculate_contributions "$contributor"
    echo # separator for readability
  done
}

# someone did an oopsie and committed under the wrong account...
declare -A user_aliases=(
    ["kyuhmi"]="kyuho"
)

contributors=(
  "diepsteven13"
  "grogers1102"
  "PhiNguyen357"
  "BLuong-vul"
  "kyuhmi"
)

print_contributions_for "${contributors[@]}"
