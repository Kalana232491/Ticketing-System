import React from 'react';

const ControlPanel = ({ onStart, onStop, startDisabled, stopDisabled }) => {
    return (
        <div className="control-panel">
            <h2>System Control</h2>

            <div className="control-buttons">
                <button
                    onClick={onStart}
                    disabled={startDisabled}
                    className="start-btn"
                >
                    Start System
                </button>

                <button
                    onClick={onStop}
                    disabled={stopDisabled}
                    className="stop-btn"
                >
                    Stop System
                </button>
            </div>
        </div>
    );
};

export default ControlPanel;
