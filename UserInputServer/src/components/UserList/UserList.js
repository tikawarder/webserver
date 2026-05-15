import React from 'react';
import UserItem from '../UserItem';
import './UserList';

function UserList({ users, loading, error, onRefresh }) {

     return (
         <div className="user-list-container">
             {/* Fejléc rész */}
             <div className="user-list-header">
                 <h2>Users</h2>
                 <button
                     onClick={onRefresh}
                     disabled={loading}
                     className="refresh-button"
                     title="Reload user list"
                 >
                     {loading ? 'Refreshing...' : '↻ Refresh'}
                 </button>
             </div>

             {/* Hibaüzenet blokk */}
             {error && (
                 <div className="error-message">
                     <strong>Error:</strong> {error}
                 </div>
             )}

             {/* Lista blokk */}
             <div className="scrollable-list">
                 {users.map(user => (
                     <UserItem
                         key={user.id}
                         id={user.id}
                         name={user.name}
                         // Kezeljük rugalmasan a backend mezőneveit (dob vs birthDay)
                         birthDay={user.dob || user.birthDay}
                         city={user.city}
                     />
                 ))}
             </div>

             {/* Üres állapot */}
             {!loading && users.length === 0 && !error && (
                 <p className="empty-message">No users found in the database.</p>
             )}
         </div>
     );
 }

 export default UserList;