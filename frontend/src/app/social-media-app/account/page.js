'use client';
import React, { useEffect, useState } from 'react';
import * as Fetch from '../../../components/Functions';



export default function Account() {
	const [activeSection, setActiveSection] = useState('General');

	// Function to render content based on active section
	const renderContent = () => {
	  switch (activeSection) {
	    case 'General':
	      return <GeneralContent />;
	    case 'Privacy':
	      return <PrivacyContent />;
	    case 'Settings':
	      return <SettingsContent />;
	    default:
	      return null;
	  }
	};

	const handleLogout = () => {
        Fetch.logout();
        window.location.href = '/social-media-app';
    };
	
	return (
		<>
		{/* temp. figure out where to move this later */}
		<div className="mt-5">
		  <button onClick={handleLogout}>Log Out</button>
		</div>

		<div style={{ display: 'flex', minHeight: '100vh' }}>
		  {/* navigation sidebar */}
		  <div style={{ width: '200px', padding: '20px', backgroundColor: '#223344' }}>
		    <ul style={{ listStyle: 'none', padding: 0 }}>
		      <li onClick={() => setActiveSection('General')} style={{ cursor: 'pointer', marginBottom: '10px' }}>
		        General
		      </li>
		      <li onClick={() => setActiveSection('Privacy')} style={{ cursor: 'pointer', marginBottom: '10px' }}>
		        Privacy
		      </li>
		      <li onClick={() => setActiveSection('Settings')} style={{ cursor: 'pointer', marginBottom: '10px' }}>
		        Settings
		      </li>
		    </ul>
		  </div>

		  {/* content area */}
		  <div style={{ flexGrow: 1, padding: '20px' }}>
		    {renderContent()}
		  </div>
		</div>
		</>
	);
}

function GeneralContent() {
  return <div><h3>General Settings</h3><p>General Settings.</p></div>;
}

function PrivacyContent() {
  return <div><h3>Privacy Settings</h3><p>Privacy Preferences</p></div>;
}

function SettingsContent() {
  return <div><h3>Account Settings</h3><p>Account settings.</p></div>;
}