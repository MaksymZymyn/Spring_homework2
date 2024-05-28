import React, { useState, useEffect } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

export default function CustomerForm({ onSave, onCancel, customer }) {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [age, setAge] = useState('');

    useEffect(() => {
        if (customer) {
            setName(customer.name);
            setEmail(customer.email);
            setAge(customer.age.toString()); // Convert age to string for proper display
        }
    }, [customer]);

    const handleSave = () => {
        if (parseInt(age) < 18) {
            alert("Enter correct age. Must be 18 or more.");
            return;
        }
        const updatedCustomer = {
            id: customer ? customer.id : null,
            name,
            email,
            age: parseInt(age), // Ensure age is converted back to integer before saving
        };
        onSave(updatedCustomer);
    };

    const handleCancel = () => {
        onCancel();
    };

    return (
        <Box sx={{ mt: 4 }}>
            <TextField
                label="Name"
                variant="outlined"
                value={name}
                onChange={(e) => setName(e.target.value)}
                fullWidth
                margin="normal"
            />
            <TextField
                label="Email"
                variant="outlined"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                fullWidth
                margin="normal"
            />
            <TextField
                label="Age"
                variant="outlined"
                type="number"
                value={age}
                onChange={(e) => setAge(e.target.value)}
                fullWidth
                margin="normal"
            />
            <Button variant="contained" onClick={handleSave}>
                {customer ? 'Save' : 'Create'}
            </Button>
            <Button variant="outlined" onClick={handleCancel} sx={{ ml: 2 }}>
                Cancel
            </Button>
        </Box>
    );
}
