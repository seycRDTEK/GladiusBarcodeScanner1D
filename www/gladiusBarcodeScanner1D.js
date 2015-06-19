cordova.define("cordova/plugin/GladiusBarcodeScanner1D", 
  function(require, exports, module) {
    var exec = require("cordova/exec");
    var GladiusBarcodeScanner1D = function() {};
    

	GladiusBarcodeScanner1D.prototype.startBarcodeListener1D = function(successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'GladiusBarcodeScanner1D', 'startBarcodeListener1D', []);
	};

	GladiusBarcodeScanner1D.prototype.stopBarcodeListener1D = function(successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'GladiusBarcodeScanner1D', 'stopBarcodeListener1D', []);
	};

	GladiusBarcodeScanner1D.prototype.startScanning1D = function(successCallback, failureCallback) {
		exec(successCallback, failureCallback, 'GladiusBarcodeScanner1D', 'startScanning1D', []);
	};
	
	var GladiusBarcodeScanner1D = new GladiusBarcodeScanner1D();
	module.exports = GladiusBarcodeScanner1D;

});


if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.GladiusBarcodeScanner1D) {
    window.plugins.GladiusBarcodeScanner1D = cordova.require("cordova/plugin/GladiusBarcodeScanner1D");
}

var gladius1D = cordova.require("cordova/plugin/GladiusBarcodeScanner1D");
gladius1D.startBarcodeListener1D(function (msg) {
    	}, function () {
    		alert("Error while receiving scan GLADIUS1D");
    	});