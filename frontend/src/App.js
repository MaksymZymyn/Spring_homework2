import React from 'react';
import './App.css';
import Dashboard from './components/Dashboard';

function App() {
  const employerId = 2;

  return (
      <div className="App">
        <Dashboard employerId={employerId} />
      </div>
  );
}

export default App;