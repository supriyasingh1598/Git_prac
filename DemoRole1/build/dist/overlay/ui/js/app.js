var ksModule = angular.module('demoapp1', ['ui.bootstrap']);

ksModule.controller('demoapp1Controller', function($scope, $http, $uibModal) {
	
    /*
    * Declared required variables
    */
	var REST_BASE_URL = 'http://localhost:8080/identityiq/plugin/rest/';
	$scope.currentUser = PluginHelper.getCurrentUsername();
    $scope.ALL_appS = [];
    $scope.ORG_appS = [];
    $scope.BSN_appS = [];
    $scope.IT_appS = [];
    $scope.SHOW_appS = [];
    $scope.ATV_INDEX = -1;
    $scope.app_TYPES = ['JDBC','webservice'];
    $scope.LOADING = false;
    $scope.userList = [];
    $scope.appCreationStatus = "";
    $scope.isCreateOpAllowed = false;
    
    /*
    * app filter
    * Param: selectedapp -> app type
    */
    $scope.changeappType = (selectedapp) => {
        if (selectedapp == '') {
            $scope.SHOW_appS = $scope.ALL_appS;
        } else if (selectedapp == 'JDBC') {
            $scope.SHOW_appS = $scope.ORG_appS;
        } else if (selectedapp == 'webservice') {
            $scope.SHOW_appS = $scope.BSN_appS;
        }
    }

    /*
    * Switch to create app page
    */
    $scope.createapp = () => {
    	$scope.ATV_INDEX = 1;
    	$scope.appCreationStatus = "";
    }
    
    /*
    * Create app function
    * Param: 
    *       appName -> Name of the app
    *       appDesc -> Description of the app
    *       selectedOwner -> Owner for the app
    *       status -> Whether the app will be enabled or not
    */
    $scope.createAppMethod = (appName, appDesc, appType, selectedOwner) => {
    	
    	var data = {
    		"name": appName,
    		"desc": appDesc,
    		"type": appType,
    		"owner": selectedOwner
    	};
    
    	var CREATE_APP_URL = "http://localhost:8080/identityiq/plugin/rest/app/createApp";
    	var headers = {
                "X-XSRF-TOKEN" : PluginHelper.getCsrfToken()
        }
    	
    	$http.post(CREATE_APP_URL, data, {headers: headers}).then(function(response) {
    		console.log(response.data);
    		$scope.appCreationStatus = response.data;
    	});
    }
    
    /*
    * Fetching all the identities display name and user name for the owner drop-down
    */
    (function(){
    	$scope.userList = [];
    	var USER_API_URL = "http://localhost:8080/identityiq/scim/v2/Users";
        var headers = {};
        
        console.log(PluginHelper.getPluginRestUrl('demoapp1'));
        
        if ($scope.currentUser == 'spadmin') {
        	this.headers = {
                    "Authorization" : `Basic ${btoa('spadmin:admin')}`
                }
        	$scope.isCreateOpAllowed = true;
        } else {
        	
        	this.headers = {
                    "Authorization" : `Basic ${btoa(`${$scope.currentUser}:xyzzy`)}`
                }
        }
        
        $http.get(USER_API_URL, {headers : headers}).then(function(response) {
            if (response.status == 200) {
               for (let i = 0; i < response.data.Resources.length; i++) {
            	   var userObj = {
            			   'displayName': response.data.Resources[i].displayName,
            			   'userName': response.data.Resources[i].userName
            	   };
            	   $scope.userList.push(userObj);
               }
            }
        }, function(error) {
            console.log(error);
        });
              
    }());
    
    /*
    * Modal popup for viewing app details
    * Param: app -> app object
    */

    
}

);

/*
* Controller for modal popup
*/
ksModule.controller('appDetailsCtrl', function($scope, $uibModalInstance, $http, appData, currentUser, userList) {

	var ctrl = this;
	
    $scope.app = appData;
    $scope.currentUser = currentUser;
    $scope.userList = userList;
    $scope.appEditStatus = "";
    
    $scope.obj = {
    	oldappName: appData.name,
    	newappName: appData.name,
    	newOwnerName: appData.owner.displayName,
    	newStatus: appData.active
    };
    
    /*
    * Close modal
    */
    $scope.close = function() {
        $uibModalInstance.close();
    };
    
});