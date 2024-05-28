import React, { useState } from 'react';

export default function AddCustomerToEmployerForm({ employerId, onSave, onCancel }) {
    const [name, setName] = useState('');
    const [age, setAge] = useState('');
    const [email, setEmail] = useState('');

    const handleSave = () => {
        onSave({ employerId, name, age, email });
        setName('');
        setAge('');
        setEmail('');
    };

    return (
        <div style={{ width: '100%', margin: '0 auto', padding: '0', display: 'flex', justifyContent: 'center' }}>
            <form onSubmit={(e) => { e.preventDefault(); handleSave(); }} style={{ width: '100%', margin: '0', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Name:</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                </div>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Age:</label>
                    <input
                        type="number"
                        value={age}
                        onChange={(e) => setAge(e.target.value)}
                        required
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                </div>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    />
                </div>
                <div>
                    <button type="submit" style={{ marginRight: '10px', padding: '8px 20px', borderRadius: '4px', background: '#007bff', color: '#fff', border: 'none' }}>Save</button>
                    <button type="button" onClick={onCancel} style={{ padding: '8px 20px', borderRadius: '4px', background: '#dc3545', color: '#fff', border: 'none' }}>Cancel</button>
                </div>
            </form>
        </div>
    );
}
