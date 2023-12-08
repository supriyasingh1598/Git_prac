/**
 * Create the module.
 */

var ksModule = angular.module('applicationonboard', []);

/**
 * Define any configs or statics
 */
//ksModule.config(function ($httpProvider) {
//    $httpProvider.defaults.xsrfCookieName = "CSRF-TOKEN";
//});


/**
 * Controller for the Kitchen Sink plugin.
 */
ksModule.controller('applicationonboardController', function($scope, $http, $uibModal) {
//$scope.welcomeMessage="Hello, World!";

$scope.viewRoleDetail = function(role) {
    $uibModal.open({
        animation: false,
        controller: 'ApplicationOnboardCtrl as ctrl',
        keyboard: false,
        templateUrl: PluginHelper.getPluginFileUrl('applicationonboard', 'ui/html/app-onboard.html'),
    });
};

}

);



