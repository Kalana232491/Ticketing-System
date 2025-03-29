import React from 'react';

const TicketStatus = ({ availableTickets, totalTickets }) => {
    const percentageAvailable = totalTickets > 0
        ? ((availableTickets / totalTickets) * 100).toFixed(2)
        : 0;

    return (
        <div className="ticket-status">
            <h2>Ticket Availability</h2>

            <div className="ticket-stats">
                <div>
                    <strong>Available Tickets:</strong> {availableTickets}
                </div>
                <div>
                    <strong>Percentage Available:</strong> {percentageAvailable}%
                </div>
            </div>

            <div className="ticket-progress">
                <div
                    className="progress-bar"
                    style={{
                        width: `${percentageAvailable}%`,
                        backgroundColor: percentageAvailable > 50 ? 'green' : percentageAvailable > 20 ? 'orange' : 'red'
                    }}
                ></div>
            </div>
        </div>
    );
};

export default TicketStatus;