import React, { useState, useEffect } from 'react';

const ConfigurationForm = ({ onInitialize, disabled, initialConfig }) => {
    const [formData, setFormData] = useState({
        maxCapacity: 20,
        totalTickets: 10,
        vendorRate: 1000,
        customerRate: 2000,
        vendorCount: 2,
        customerCount: 2
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (initialConfig) {
            setFormData(prevData => ({
                ...prevData,
                ...initialConfig
            }));
        }
    }, [initialConfig]);

    const validateForm = () => {
        const newErrors = {};
        const { maxCapacity, totalTickets, vendorRate, customerRate, vendorCount, customerCount } = formData;

        // Validate each field
        if (maxCapacity <= 0) {
            newErrors.maxCapacity = 'Max capacity must be greater than 0';
        }

        if (totalTickets <= 0) {
            newErrors.totalTickets = 'Total tickets must be greater than 0';
        }

        if (vendorRate <= 0) {
            newErrors.vendorRate = 'Vendor rate must be greater than 0';
        }

        if (customerRate <= 0) {
            newErrors.customerRate = 'Customer rate must be greater than 0';
        }

        if (vendorCount <= 0) {
            newErrors.vendorCount = 'Vendor count must be greater than 0';
        }

        if (customerCount <= 0) {
            newErrors.customerCount = 'Customer count must be greater than 0';
        }

        // Additional validation: max capacity must be greater than total tickets
        if (maxCapacity <= totalTickets) {
            newErrors.maxCapacity = 'Max capacity must be greater than total tickets';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: parseInt(value) || 0
        }));

        // Clear previous error when user starts typing
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: undefined
            }));
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (validateForm()) {
            onInitialize(formData);
        }
    };

    return (
        <form
            className="configuration-form"
            onSubmit={handleSubmit}
            style={{ opacity: disabled ? 0.5 : 1 }}
        >
            <h2>System Configuration</h2>

            <div className="form-grid">
                {Object.entries(formData).map(([key, value]) => (
                    <div key={key} className="form-group">
                        <label htmlFor={key}>
                            {key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase())}
                        </label>
                        <input
                            type="number"
                            id={key}
                            name={key}
                            value={value}
                            onChange={handleChange}
                            disabled={disabled}
                            min="1"
                            required
                        />
                        {errors[key] && (
                            <div style={{ color: 'red', fontSize: '0.8em', marginTop: '5px' }}>
                                {errors[key]}
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <button
                type="submit"
                disabled={disabled}
                className="submit-btn"
            >
                Initialize System
            </button>
        </form>
    );
};

export default ConfigurationForm;