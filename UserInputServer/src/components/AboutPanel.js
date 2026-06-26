import React from 'react';

function AboutPanel() {
    return (
        <div style={{ width: '100%', maxWidth: '420px', textAlign: 'left' }}>
            <h2 style={{ marginTop: 0, color: '#333' }}>About this app</h2>
            <p style={{ color: '#555', lineHeight: '1.6' }}>
                A full-stack microservices demo built with Spring Boot and React.
                Log in to register and manage people in the database.
            </p>
            <ul style={{ color: '#555', lineHeight: '2', paddingLeft: '20px', margin: '12px 0' }}>
                <li>🔐 JWT authentication via Spring Security</li>
                <li>📡 Spring Cloud Gateway routing</li>
                <li>📨 Kafka event streaming (outbox pattern)</li>
                <li>🔁 Resilience4j circuit breaker</li>
                <li>🔍 Distributed tracing with Zipkin</li>
            </ul>
            <p style={{ color: '#888', fontSize: '0.85em', marginBottom: 0 }}>
                ← Log in to get started
            </p>
        </div>
    );
}

export default AboutPanel;
