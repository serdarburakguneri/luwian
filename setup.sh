#!/bin/bash

# Luwian CLI Setup Script
# This script makes the luwian command globally available

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN} Setting up Luwian CLI...${NC}"

# Check if we're in the right directory
if [ ! -d "luwian-cli" ]; then
    echo -e "${RED} Error: Please run this script from the Luwian project root directory${NC}"
    echo "Expected to find 'luwian-cli' directory here"
    exit 1
fi

# Create the luwian function
LUWIAN_FUNCTION='luwian() { 
    local original_dir=$(pwd)
    cd "$(dirname "$0")/luwian-cli" 2>/dev/null || {
        echo "Error: Cannot find luwian-cli directory. Make sure you are in the Luwian project root."
        return 1
    }
    mvn -q -ntp -B -DskipTests exec:java -Dexec.mainClass=io.luwian.cli.LuwianCli -Dexec.args="$*"
    cd "$original_dir"
}'

# Detect shell and add to appropriate config file
SHELL_CONFIG=""
if [ -n "$ZSH_VERSION" ]; then
    SHELL_CONFIG="$HOME/.zshrc"
elif [ -n "$BASH_VERSION" ]; then
    SHELL_CONFIG="$HOME/.bashrc"
else
    echo -e "${YELLOW}  Warning: Could not detect shell. Please manually add the function to your shell config.${NC}"
    echo "Add this to your shell configuration file:"
    echo "$LUWIAN_FUNCTION"
    exit 0
fi

# Check if function already exists
if grep -q "luwian()" "$SHELL_CONFIG" 2>/dev/null; then
    echo -e "${YELLOW}  Luwian function already exists in $SHELL_CONFIG${NC}"
    echo -e "${GREEN} Setup complete! You can use 'luwian' command now.${NC}"
    echo ""
    echo "Example usage:"
    echo "  luwian \"new service orders --pkg com.acme.orders --http-port 8080 --dir ..\""
    echo "  luwian \"add handler Order --pkg com.acme.orders --path /api/orders --arch layered\""
    exit 0
fi

# Add function to shell config
echo "" >> "$SHELL_CONFIG"
echo "# Luwian CLI function" >> "$SHELL_CONFIG"
echo "$LUWIAN_FUNCTION" >> "$SHELL_CONFIG"

echo -e "${GREEN} Luwian function added to $SHELL_CONFIG${NC}"

# Try to reload the shell configuration
echo -e "${YELLOW} Reloading shell configuration...${NC}"
if source "$SHELL_CONFIG" 2>/dev/null; then
    echo -e "${GREEN} Setup complete! You can use 'luwian' command now.${NC}"
else
    echo -e "${YELLOW} Please restart your terminal or run: source $SHELL_CONFIG${NC}"
fi

echo ""
echo "Example usage:"
echo "  luwian \"new service orders --pkg com.acme.orders --http-port 8080 --dir ..\""
echo "  luwian \"add handler Order --pkg com.acme.orders --path /api/orders --arch layered\""
echo ""
echo -e "${GREEN}🎉Happy coding!${NC}"
