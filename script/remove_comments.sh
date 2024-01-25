#!/bin/bash

# Check if the input file is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <input_file>"
    exit 1
fi

input_file=$1
output_file="${input_file%.properties}_no_comments.properties"

# Remove comments using sed
sed '/^[[:space:]]*#/d; /^[[:space:]]*![[:space:]]/d; s/[[:space:]]*#[^"]*//; s/[[:space:]]*![^"]*//' "$input_file" > "$output_file"

rm $input_file
mv $output_file $input_file
