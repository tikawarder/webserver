(function() {
    console.log(a); // undefined
    var a = 10;
})();

greet(); // működik!

function greet() {
    console.log("Hello!");
}
