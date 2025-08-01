import React from 'https://esm.sh/react@18';

export default function Welcome(props) {
    return React.createElement('h1', null, `Szia, ${props.name}!`);
}