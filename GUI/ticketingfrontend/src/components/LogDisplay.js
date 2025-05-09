import React from 'react';

const LogDisplay = ({ logs }) => {
    return (
        <div className="log-display">
            <h2>System Logs</h2>

            <div className="log-container">
                {logs.length === 0 ? (
                    <p>No logs available</p>
                ) : (
                    <ul>
                        {logs.map((log, index) => (
                            <li key={index} className="log-entry">
                                {log}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default LogDisplay;

