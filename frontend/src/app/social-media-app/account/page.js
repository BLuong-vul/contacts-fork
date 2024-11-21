'use client';
import React, { useEffect, useState } from 'react';
import * as Fetch from '../../../components/Functions';
import { FaEdit, FaCheck } from 'react-icons/fa'; 


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
	
	return (
		<>
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
  const [userInfo, setUserInfo] = useState({
    displayName: '',
    bio: '',
    location: '',
    qualifications: '',
    occupation: '',
  })
  // const [displayName, setDisplayName] = useState('');
  // const [bio, setBio] = useState('');
  // const [location, setLocation] = useState('');
  // const [qualifications, setQualifications] = useState('');
  // const [occupation, setOccupation] = useState('');
  const [isEditing, setIsEditing] = useState({
    displayName: false,
    bio: false,
    location: false,
    qualifications: false,
    occupation: false
  });
  // const [isEditingDisplayName, setIsEditingDisplayName] = useState(false);
  // const [isEditingBio, setIsEditingBio] = useState(false);
  // const [isEditingLocation, setIsEditingLocation] = useState(false);
  // const [isEditingQualifications, setIsEditingQualifications] = useState(false);
  // const [isEditingOccupation, setIsEditingOccupation] = useState(false);
  const [show, setShow] = useState({
    location: false,
    qualifications: false,
    occupation: false,
    birthdate: false
  });
  // const [showLocation, setShowLocation] = useState(false);
  // const [showQualifications, setShowQualifications] = useState(false);
  // const [showOccupation, setShowOccupation] = useState(false);
  // const [showBirthday, setShowBirthday] = useState(false);


  // Fetch user data on mount
  useEffect(() => {
      const fetchUserId = async () => {
          try {
              const currentUserInfo = await Fetch.getCurrentUserInfo();
              setUserInfo(currentUserInfo);
              console.log(currentUserInfo);
          } catch (error) {
              console.error('Error fetching ID:', error);
              throw error;
          }
      };
      fetchUserId();
  }, []);

  // On submit any text field
  const handleFieldSubmit = async (field) => {
    if (isEditing[field]){
      await Fetch[`update${field.charAt(0).toUpperCase() + field.slice(1)}`](userInfo[field]);
    }
    setIsEditing(prevState => ({
          ...prevState,
          [field]: !prevState[field]
    }));
  }

  // On change to any text field
  const handleChange = (e, field) => {
    setUserInfo(prevState => ({
      ...prevState,
      [field]: e.target.value
    }));
  };

  // Toggle for checkboxes
  const toggleShow = (field) => {
    setShow(prevState => ({
      ...prevState,
      [field]: !prevState[field]
    }));
  };

  const handleLogout = () => {
    Fetch.logout();
    window.location.href = '/social-media-app';
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>
      {/* Display Name */}
      <label style={{ flex: 1 }}>Display Name:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={userInfo.displayName}
          onChange={(e) => handleChange(e, 'displayName')}
          readOnly={!isEditing.displayName}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditing.displayName ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.displayName ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('displayName')} style={{ marginLeft: '10px' }}>
          {isEditing.displayName ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Bio */}
      <label style={{ flex: 1 }}>Bio:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <textarea
          value={userInfo.bio}
          onChange={(e) => handleChange(e, 'bio')}
          readOnly={!isEditing.bio}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            minHeight: '80px',
            backgroundColor: isEditing.bio ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.bio ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('bio')} style={{ marginLeft: '10px' }}>
          {isEditing.bio ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

      {/* Location */}
      <label style={{ flex: 1 }}>Location:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={userInfo.location}
          onChange={(e) => handleChange(e, 'location')}
          readOnly={!isEditing.location}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditing.location ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.location ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('location')} style={{ marginLeft: '10px' }}>
          {isEditing.location ? <FaCheck /> : <FaEdit />}
        </button>
      </div>
      <label style={{ display: 'block', marginTop: '0px', marginBottom: '15px' }}>
          <input
            type="checkbox"
            checked={show.location}
            onChange={() => toggleShow('location')}
          />
          Display on profile
      </label>

      {/* Occupation */}
      <label style={{ flex: 1 }}>Occupation:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '5px' }}>
        <input
          type="text"
          value={userInfo.occupation}
          onChange={(e) => handleChange(e, 'occupation')}
          readOnly={!isEditing.occupation}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditing.occupation ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditing.occupation ? 'text' : 'default',
          }}
        />
        <button onClick={() => handleFieldSubmit('occupation')} style={{ marginLeft: '10px' }}>
          {isEditing.occupation ? <FaCheck /> : <FaEdit />}
        </button>
      </div>
      <label style={{ display: 'block', marginBottom: '15px' }}>
        <input
          type="checkbox"
          checked={show.occupation}
          onChange={() => toggleShow('occupation')}
        />
        Display on profile
      </label>

      {/* Birthday display checkbox */}
      <div style={{ marginTop: '20px', marginBottom: '20px' }}>
        <label style={{ display: 'block' }}>
          <input
            type="checkbox"
            checked={show.birthdate}
            onChange={() => toggleShow('birthdate')}
          />
          Display birthdate on profile
        </label>
      </div>

      {/* Profile picture / banner */}
      <div style={{ display: 'flex', gap: '15px', marginBottom: '20px' }}>
        <button style={{ padding: '10px', cursor: 'pointer' }}>Change Profile Picture</button>
        <button style={{ padding: '10px', cursor: 'pointer' }}>Change Profile Banner</button>
      </div>

      {/* Logout button */}
      <div className="mt-5">
        <button onClick={handleLogout}>Log Out</button>
      </div>
    </div>
  );
}



function PrivacyContent() {
      // state for buttons/checkbox
      const [messagesOption, setMessagesOption] = useState('');
      const [requestsOption, setRequestsOption] = useState(false);
      
      const handleChange = (event) => {
        setMessagesOption(event.target.value);
      };


      return (
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
          <div style={{ marginTop: '15px', marginBottom: '15px' }}>
            <label style={{ flex: 1 }}>Messages</label>
            <label style={{ display: 'block', marginTop: '10px' }}>
              <input
                type="radio"
                value="option1"
                checked={messagesOption === 'option1'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Allow messages from anyone
            </label>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option2"
                checked={messagesOption === 'option2'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Allow messages from mutuals only
            </label>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option3"
                checked={messagesOption === 'option3'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Allow messages from followers only
            </label>
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginTop: '5px' }}>
              <input
                type="radio"
                value="option4"
                checked={messagesOption === 'option4'}
                onChange={handleChange}
                style={{marginRight: '8px'}}
              />
              Disable messages
            </label>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block' }}>
              <input
                type="checkbox"
                checked={requestsOption}
                onChange={() => setRequestsOption(!requestsOption)}
                style={{marginRight: '8px'}}
              />
              Blocked messages become requests
            </label>
          </div>
        </div>
      );
}

function SettingsContent() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [isEditingUsername, setIsEditingUsername] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);

  const handleDelete = () => {
    
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto' }}>

      {/* Username */}
      <label style={{ flex: 1 }}>Username:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          readOnly={!isEditingUsername}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingUsername ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingUsername ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingUsername(!isEditingUsername)} style={{ marginLeft: '10px' }}>
          {isEditingUsername ? <FaCheck /> : <FaEdit />}
        </button>
      </div>

    {/* Password */}
      <label style={{ flex: 1 }}>Password:</label>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '15px' }}>
        <input
          type="text"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          readOnly={!isEditingPassword}
          style={{
            width: '100%',
            padding: '8px',
            marginTop: '5px',
            backgroundColor: isEditingPassword ? '#fff' : '#a0a0a0',
            color: 'black',
            cursor: isEditingPassword ? 'text' : 'default',
          }}
        />
        <button onClick={() => setIsEditingUPassword(!isEditingPassword)} style={{ marginLeft: '10px' }}>
          {isEditingPassword ? <FaCheck /> : <FaEdit />}
        </button>
      </div>



      {/* Delete Account */}
      <div className="mt-5">
        <button onClick={handleDelete}>Delete Account</button>
      </div>
    </div>
  );
}