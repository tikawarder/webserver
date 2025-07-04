// src/components/UserList.js
import React from 'react';
import UserItem from './UserItem'; // 1. Lépés: Importáljuk a komponenst

function UserList({ users, loading, error, onRefresh }) {

    return (
        <div style={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column' }}>

            {/* Fejléc és Frissítés gomb */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                <h2 style={{ margin: 0 }}>Felhasználók</h2>

                <button
                    onClick={onRefresh}
                    disabled={loading}
                    style={{
                        padding: '5px 10px',
                        cursor: loading ? 'not-allowed' : 'pointer',
                        backgroundColor: '#28a745',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px'
                    }}
                >
                    {loading ? 'Frissítés...' : '↻ Frissítés'}
                </button>
            </div>

            {/* Hibaüzenet */}
            {error && <p style={{ color: 'red' }}>Hiba: {error}</p>}

            {/* 2. Lépés: A Lista renderelése UserItem-ekkel */}
            {/* Itt ul helyett div-et használok konténernek, mert a UserItem egy kártya */}
            <div style={{ flex: 1, overflowY: 'auto' }}>
                {users.map(user => (
                    <UserItem
                        key={user.id}       // A Reactnek szüksége van az egyedi kulcsra
                        id={user.id}
                        name={user.name}
                        birthDay={user.birthDay} // FIGYELEM: Az adatbázisban "dob", a komponensben "birthDay" a neve!
                        city={user.city}
                    />
                ))}
            </div>

            {/* Üres állapot */}
            {!loading && users.length === 0 && !error && (
                <p>Nincs megjeleníthető adat.</p>
            )}
        </div>
    );
}

export default UserList;