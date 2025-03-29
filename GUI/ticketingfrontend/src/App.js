import React, { useState, useEffect } from 'react';
import ConfigurationForm from './components/ConfigurationForm';
import TicketStatus from './components/TicketStatus';
import ControlPanel from './components/ControlPanel';
import LogDisplay from './components/LogDisplay';
import './App.css';

const App = () => {
  const [config, setConfig] = useState(null);
  const [availableTickets, setAvailableTickets] = useState(0);
  const [logs, setLogs] = useState([]);
  const [error, setError] = useState(null);
  const [systemStatus, setSystemStatus] = useState('Not Initialized');
  const [isSoldOut, setIsSoldOut] = useState(false);

  const fetchWrapper = async (url, options = {}) => {
    try {
      const response = await fetch(url, options);

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `HTTP Error: ${response.status}`);
      }

      const contentType = response.headers.get('Content-Type') || '';
      if (contentType.includes('application/json')) {
        return await response.json(); // Parse as JSON if Content-Type is JSON
      }

      // Handle plain text responses
      return await response.text();
    } catch (err) {
      setError(err.message);
      throw err; // Re-throw for additional handling if needed
    }
  };

  const loadSavedConfiguration = async () => {
    try {
      const savedConfig = await fetchWrapper('http://localhost:8080/api/ticketingsystem/load-config');
      setConfig(savedConfig);
      return savedConfig;
    } catch (err) {
      console.error('Failed to load saved configuration:', err);
      return null;
    }
  };

  const handleInitialize = async (configData) => {
    try {
      const data = await fetchWrapper('http://localhost:8080/api/ticketingsystem/initialize', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(configData),
      });

      // Handle both JSON and text responses
      const logMessage = typeof data === 'string' ? data : data.message || 'System initialized';
      setConfig(configData);
      setSystemStatus('Initialized');
      setLogs((prev) => [...prev, logMessage]);
      setError(null);
      setIsSoldOut(false);
    } catch (err) {
      setSystemStatus('Error');
    }
  };

  const handleStart = async () => {
    if (!config) {
      setError('Please initialize the system first');
      return;
    }

    try {
      const data = await fetchWrapper('http://localhost:8080/api/ticketingsystem/start', { method: 'POST' });

      const logMessage = typeof data === 'string' ? data : data.message || 'System started';
      setSystemStatus('Running');
      setLogs((prev) => [...prev, logMessage]);
      setError(null);
    } catch (err) {
      setSystemStatus('Error');
    }
  };

  const handleStop = async () => {
    try {
      const data = await fetchWrapper('http://localhost:8080/api/ticketingsystem/stop', { method: 'POST' });

      const logMessage = typeof data === 'string' ? data : data.message || 'System stopped';
      setSystemStatus('Stopped');
      setLogs((prev) => [...prev, logMessage]);
      setError(null);
      setAvailableTickets(0);
    } catch (err) {
      setSystemStatus('Error');
    }
  };

  useEffect(() => {
    let ticketInterval;
    let logInterval;
    let soldOutInterval;
    loadSavedConfiguration();



    const fetchTickets = async () => {
      if (systemStatus === 'Running') {
        try {
          const tickets = await fetchWrapper('http://localhost:8080/api/ticketingsystem/tickets');
          setAvailableTickets(tickets);
        } catch (err) {
          setError('Failed to fetch tickets');
        }
      }
    };

    const checkSoldOut = async () => {
      if (systemStatus === 'Running') {
        try {
          const soldOut = await fetchWrapper('http://localhost:8080/api/ticketingsystem/soldout');
          if (soldOut) {
            setIsSoldOut(true);
            setSystemStatus('Stopped');
          }
        } catch (err) {
          console.error('Failed to check sold-out status');
        }
      }
    };

    const fetchLogs = async () => {
      if (systemStatus === 'Running') {
        try {
          const systemLogs = await fetchWrapper('http://localhost:8080/api/ticketingsystem/logs');
          setLogs(systemLogs);
        } catch (err) {
          console.error('Failed to fetch logs');
        }
      }
    };

    if (systemStatus === 'Running') {
      ticketInterval = setInterval(fetchTickets, 2000);
      logInterval = setInterval(fetchLogs, 2000);
      soldOutInterval = setInterval(checkSoldOut, 2000);
    }

    return () => {
      clearInterval(ticketInterval);
      clearInterval(logInterval);
      clearInterval(soldOutInterval);
    };
  }, [systemStatus]);

  return (
      <div className="app-container">
        <h1>Ticket Vending System</h1>

        {error && <div className="error-banner"><p>Error: {error}</p></div>}

        {isSoldOut && (
            <div className="sold-out-banner">
              <h2> ALL TICKETS SOLD OUT! </h2>
            </div>
        )}

        <div className="system-status">
          System Status:
          <span className={`status-indicator ${systemStatus.toLowerCase()}`}>
          {systemStatus}
        </span>
        </div>

        <ConfigurationForm
            onInitialize={handleInitialize}
            disabled={systemStatus !== 'Not Initialized' && systemStatus !== 'Stopped'}
            initialConfig={config} // Pass saved config to form
        />

        <TicketStatus
            availableTickets={availableTickets}
            totalTickets={config?.totalTickets || 0}
        />

        <ControlPanel
            onStart={handleStart}
            onStop={handleStop}
            startDisabled={systemStatus !== 'Initialized' && systemStatus !== 'Stopped'}
            stopDisabled={systemStatus !== 'Running'}
        />

        <LogDisplay logs={logs} />
      </div>
  );
};

export default App;