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
  // Temporary state holders
    const [displayName, setDisplayName] = useState('');
    const [bio, setBio] = useState('');
    const [location, setLocation] = useState('');
    const [education, setEducation] = useState('');
    const [workplace, setWorkplace] = useState('');
    
    // State for checkboxes
    const [showLocation, setShowLocation] = useState(false);
    const [showEducation, setShowEducation] = useState(false);
    const [showWorkplace, setShowWorkplace] = useState(false);
    const [showBirthday, setShowBirthday] = useState(false);

    return (
      <div style={{ maxWidth: '600px', margin: '0 auto' }}>
        <h3>General Settings</h3>

        {/* display name */}
        <div style={{ marginBottom: '15px' }}>
          <label>Display Name:</label>
          <input
            type="text"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>

        {/* bio */}
        <div style={{ marginBottom: '15px' }}>
          <label>Bio:</label>
          <textarea
            value={bio}
            onChange={(e) => setBio(e.target.value)}
            style={{ width: '100%', padding: '8px', marginTop: '5px', minHeight: '80px' }}
          />
        </div>

        {/* optional fields */}
        <h4>Optional Information</h4>

        <div style={{ marginBottom: '15px' }}>
          <label>Location:</label>
          <input
            type="text"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
          <label style={{ display: 'block', marginTop: '5px' }}>
            <input
              type="checkbox"
              checked={showLocation}
              onChange={() => setShowLocation(!showLocation)}
            />
            Display on profile
          </label>
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Education:</label>
          <input
            type="text"
            value={education}
            onChange={(e) => setEducation(e.target.value)}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
          <label style={{ display: 'block', marginTop: '5px' }}>
            <input
              type="checkbox"
              checked={showEducation}
              onChange={() => setShowEducation(!showEducation)}
            />
            Display on profile
          </label>
        </div>

        <div style={{ marginBottom: '15px' }}>
          <label>Workplace:</label>
          <input
            type="text"
            value={workplace}
            onChange={(e) => setWorkplace(e.target.value)}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
          <label style={{ display: 'block', marginTop: '5px' }}>
            <input
              type="checkbox"
              checked={showWorkplace}
              onChange={() => setShowWorkplace(!showWorkplace)}
            />
            Display on profile
          </label>
        </div>

        {/* birthday display checkbox */}
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block' }}>
            <input
              type="checkbox"
              checked={showBirthday}
              onChange={() => setShowBirthday(!showBirthday)}
            />
            Display birthday on profile
          </label>
        </div>

        {/* profile picture / profile banner */}
        <div style={{ display: 'flex', gap: '15px', marginBottom: '20px' }}>
          <button style={{ padding: '10px', cursor: 'pointer' }}>Change Profile Picture</button>
          <button style={{ padding: '10px', cursor: 'pointer' }}>Change Profile Banner</button>
        </div>
      </div>
    );
}

function PrivacyContent() {
  return <div><h3>Privacy Settings</h3><p>Privacy Preferences</p></div>;
}

function SettingsContent() {
  return <div><h3>Account Settings</h3><p>Account settings.</p></div>;
}