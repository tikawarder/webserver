
export function greetUser(name, callback) {
    console.log("Hi " + name);
    callback();
}
export const func = function (message){
    alert(message + " called!");
}
//greetUser("Tamás",()=>func("callback"))

export const obj = {
    name: "object",
    regularFunc: function() { alert(this.name); },
    arrowFunc: () => { alert(window.color); }
};

//obj.regularFunc(); // "Objektum"
//obj.arrowFunc();   // undefined vagy globális objektum neve (window vagy undefined strict módban)

export default function makeCounter() {
    let count = 0;
    return function() {
        count++;
        return count;
    };
}

const counter = makeCounter();
// console.log(counter()); // 1
// console.log(counter()); // 2

const person = {
    name: "Anna",
    age: 30,
    city: "Budapest"
};

// Klasszikus mód:
const name1 = person.name;
const age1 = person.age;

// Destructuring:
const { name, age } = person;

// console.log(name); // "Anna"
// console.log(age);  // 30