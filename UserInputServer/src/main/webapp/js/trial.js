(function() {
    console.log(a); // undefined
    var a = 10;
})();

greet();

function greet() {
    console.log("Hello!");
}
