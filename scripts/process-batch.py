import requests
import json
import os
import time

# API endpoint
url = "http://localhost:8080/flux/process" 

# Headers
headers = {
    "Content-Type": "application/json"
}

def load_transactions():
    """Load transactions from JSON file"""
    json_file_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "transactions.json")
    
    try:
        with open(json_file_path, 'r', encoding='utf-8') as file:
            transactions = json.load(file)
            print(f"Loaded {len(transactions)} transactions from file")
            return transactions
    except FileNotFoundError:
        print(f"File not found: {json_file_path}")
        return []
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON: {e}")
        return []

def process_transaction(transaction, index):
    """Process a single transaction"""
    print(f"\n--- Processing Transaction {index + 1} (ID: {transaction.get('transactionId', 'Unknown')}) ---")
    
    start_time = time.time()
    try:
        # Make the POST request
        response = requests.post(url, json=transaction, headers=headers)
        
        # Check response status
        if response.status_code == 200:
            result = response.json()
            print("✅ Transaction processed successfully:")
            print(json.dumps(result, indent=2))
            return True
        elif response.status_code == 504:
            print("⏰ Request timed out (504 Gateway Timeout)")
        elif response.status_code == 500:
            print("❌ Internal server error (500)")
        else:
            print(f"⚠️ Unexpected status code: {response.status_code}")
            print(f"Response: {response.text}")
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Request failed: {e}")
    
    duration = time.time() - start_time
    print(f"⏱️ Execution duration: {duration:.3f} seconds")
    return False

def main():
    """Main function to process all transactions"""
    # Load transactions from file
    transactions = load_transactions()
    
    if not transactions:
        print("No transactions to process.")
        return
    
    # Process each transaction
    successful_count = 0
    failed_count = 0
    start_time = time.time()
    
    for index, transaction in enumerate(transactions):
        success = process_transaction(transaction, index)
        
        if success:
            successful_count += 1
        else:
            failed_count += 1
    
    # Summary
    duration = time.time() - start_time
    print(f"\n--- Processing Summary ---")
    print(f"Total transactions: {len(transactions)}")
    print(f"Successful: {successful_count}")
    print(f"Failed: {failed_count}")
    print(f"Process duration: {duration:.3f} seconds")

if __name__ == "__main__":
    main()