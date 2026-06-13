import React from 'react';
import UserItem from '../UserItem';
import './UserList';

function UserList({ users, loading, error, onRefresh }) {

     return (
         <div className="user-list-container">
             {/* Header section */}
             <div className="user-list-header">
                 <h2>Users</h2>
                 <button
                     onClick={onRefresh}
                     disabled={loading}
                     className="refresh-button"
                     data-testid="refresh-button"
                     title="Reload user list"
                 >
                     {loading ? 'Refreshing...' : '↻ Refresh'}
                 </button>
             </div>

             {/* Error message block */}
             {error && (
                 <div className="error-message">
                     <strong>Error:</strong> {error}
                 </div>
             )}

             {/* List block */}
             <div className="scrollable-list" data-testid="user-list">
                 {users.map(user => (
                     <UserItem
                         key={user.id}
                         id={user.id}
                         name={user.name}
                         // Handle backend field names flexibly (dob vs birthDay)
                         birthDay={user.dob || user.birthDay}
                         city={user.city}
                         data-testid="user-item"
                     />
                 ))}
             </div>

             {/* Empty state */}
             {!loading && users.length === 0 && !error && (
                 <p className="empty-message" data-testid="empty-state">No users found in the database.</p>
             )}
         </div>
     );
 }

 export default UserList;