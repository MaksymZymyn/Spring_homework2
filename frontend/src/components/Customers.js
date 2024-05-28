import React, { useEffect, useState } from 'react';
import Link from '@mui/material/Link';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Title from './Title';
import AccountForm from './AccountForm';
import CustomerForm from './CustomerForm';

export default function Customers() {
    const [customers, setCustomers] = useState([]);
    const [editingCustomerId, setEditingCustomerId] = useState(null);
    const [addingAccountCustomerId, setAddingAccountCustomerId] = useState(null);

    const handleDeposit = async (customerId, accountNumber, amount) => {
        try {
            const response = await fetch(`http://localhost:9000/accounts/deposit/${accountNumber}?amount=${amount}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                alert('Deposit successful');
                // Optionally, you can fetch updated customer data or perform any other necessary action here
            } else {
                const errorMessage = await response.text();
                alert(`Failed to deposit amount: ${errorMessage}`);
            }
        } catch (error) {
            console.error('Error depositing amount:', error);
            alert('Error depositing amount.');
        }
    };

    const handleWithdraw = async (customerId, accountNumber, amount) => {
        try {
            const response = await fetch(`http://localhost:9000/accounts/withdrawal/${accountNumber}?amount=${amount}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                alert('Withdrawal successful');
                // Optionally, you can fetch updated customer data or perform any other necessary action here
            } else {
                const errorMessage = await response.text();
                alert(`Failed to withdraw amount: ${errorMessage}`);
            }
        } catch (error) {
            console.error('Error withdrawing amount:', error);
            alert('Error withdrawing amount.');
        }
    };

    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomers = async () => {
        try {
            const response = await fetch('http://localhost:9000/customers');
            if (!response.ok) {
                throw new Error('Failed to fetch customers');
            }
            const data = await response.json();
            setCustomers(data);
        } catch (error) {
            console.error('Error fetching customers:', error);
            // Handle error here (e.g., show a message to the user)
        }
    };

    const updateCustomer = async (customerId, updatedCustomer) => {
        try {
            const response = await fetch(`http://localhost:9000/customers/${customerId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(updatedCustomer),
            });

            if (response.ok) {
                fetchCustomers();
                setEditingCustomerId(null);
                alert('Customer updated successfully.');
            } else {
                alert('Failed to update customer. Please try again.');
            }
        } catch (error) {
            console.error('Error updating customer:', error);
            alert('Error updating customer.');
        }
    };

    const deleteCustomer = async (customerId) => {
        try {
            const response = await fetch(`http://localhost:9000/customers/${customerId}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                fetchCustomers();
                alert('Customer deleted successfully.');
            } else {
                alert('Failed to delete customer. Please try again.');
            }
        } catch (error) {
            console.error('Error deleting customer:', error);
            alert('Error deleting customer.');
        }
    };

    const addAccountToCustomer = async (customerId, currency) => {
        try {
            const response = await fetch(`http://localhost:9000/customers/${customerId}/accounts`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ currency }),
            });

            if (response.ok) {
                fetchCustomers();
                setAddingAccountCustomerId(null);
                alert('Account added successfully.');
            } else {
                alert('Failed to add account. Please try again.');
            }
        } catch (error) {
            console.error('Error adding account to customer:', error);
            alert('Error adding account.');
        }
    };

    const handleEditButtonClick = (customerId) => {
        setEditingCustomerId(customerId);
    };

    const handleCancelEdit = () => {
        setEditingCustomerId(null);
    };

    const handleSaveCustomer = (customer) => {
        if (editingCustomerId) {
            updateCustomer(editingCustomerId, customer);
        }
    };

    const handleSaveNewAccount = (customerId, currency) => {
        if (currency) {
            addAccountToCustomer(customerId, currency);
        }
    };

    return (
        <React.Fragment>
            {customers && customers.length > 0 ? (
                customers.map((customer) => (
                    <React.Fragment key={customer.id}>
                        {editingCustomerId === customer.id ? (
                            <React.Fragment>
                                <Title>Editing {customer.name}</Title>
                                <CustomerForm
                                    onSave={handleSaveCustomer}
                                    onCancel={handleCancelEdit}
                                    customer={customer}
                                />
                            </React.Fragment>
                        ) : (
                            <React.Fragment>
                                <Title>
                                    {customer.name} (email: {customer.email}, age: {customer.age})
                                </Title>
                                <Table size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Account Number</TableCell>
                                            <TableCell>Currency</TableCell>
                                            <TableCell>Balance</TableCell>
                                            <TableCell>Actions</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {customer.accounts && Array.isArray(customer.accounts) && customer.accounts.length > 0 ? (
                                            customer.accounts.map((account) => (
                                                <TableRow key={account.id}>
                                                    <TableCell>{account.number}</TableCell>
                                                    <TableCell>{account.currency}</TableCell>
                                                    <TableCell>{account.balance}</TableCell>
                                                    <TableCell>
                                                        <button onClick={() => handleDeposit(customer.id, account.number)}>Deposit</button>
                                                        <button onClick={() => handleWithdraw(customer.id, account.number)}>Withdraw</button>
                                                    </TableCell>
                                                </TableRow>
                                            ))
                                        ) : (
                                            <TableRow>
                                                <TableCell colSpan={4}>No accounts available</TableCell>
                                            </TableRow>
                                        )}
                                    </TableBody>
                                </Table>
                                {addingAccountCustomerId === customer.id ? (
                                    <AccountForm
                                        onSave={(currency) => handleSaveNewAccount(customer.id, currency)}
                                        onCancel={() => setAddingAccountCustomerId(null)}
                                        customer={customer}
                                    />
                                ) : (
                                    <Link color="primary" href="#" onClick={() => setAddingAccountCustomerId(customer.id)} sx={{ mt: 3 }}>Add Account</Link>
                                )}
                                <Link color="primary" href="#" onClick={() => handleEditButtonClick(customer.id)} sx={{ mt: 3 }}>Edit Customer</Link>
                                <button onClick={() => deleteCustomer(customer.id)}>Delete Customer</button>
                            </React.Fragment>
                        )}
                    </React.Fragment>
                ))
            ) : (
                <div>No customers available</div>
            )}
        </React.Fragment>
    );
}
