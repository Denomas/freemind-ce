#!/bin/bash
# PreToolUse hook: Block Grep on .java/.kts files, redirect to Serena MCP
INPUT=$(cat)
GLOB=$(echo "$INPUT" | jq -r '.tool_input.glob // empty')
PVAL=$(echo "$INPUT" | jq -r '.tool_input.path // empty')
TVAL=$(echo "$INPUT" | jq -r '.tool_input.type // empty')
if echo "$GLOB $PVAL $TVAL" | grep -qiE '(\.java|type.*java)'; then
  echo '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"BLOCKED: Use Serena MCP to search Java code: search_for_pattern(pattern) or find_symbol(name). See AGENTS.md."}}'
fi
