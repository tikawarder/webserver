import {obj, func} from './learn.js'
import doing from './learn.js'

window.color = "blue";

document.getElementById("myPara").addEventListener("click", function() {
    alert("Button was clicked!");
});

obj.regularFunc();
obj.arrowFunc();

const counter1 = doing();  // létrehoz egy új számlálót

console.log(counter1()); // 1
console.log(counter1()); // 2
console.log(counter1()); // 3

const counter2 = doing();  // új, különálló számláló

console.log(counter2()); // 1