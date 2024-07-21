angular.module('mrlapp.service.WikiDataFetcherGui', []).controller('WikiDataFetcherGuiCtrl', ['$scope', '$log', 'mrl', function($scope, $log, mrl) {
    $log.info('WikiDataFetcherGuiCtrl')
    var _self = this
    var msg = this.msg

    this.updateState = function(service) {
		$scope.service = service
	}

    // console.log('mary', $scope.service)

    this.onMsg = function(inMsg) {
    	let data = inMsg.data[0]
        switch (inMsg.method) {
        case 'onState':
            _self.updateState(data)
            $scope.$apply()
            break
        default:
            $log.error("ERROR - unhandled method " + $scope.name + " " + inMsg.method)
            break
        }
    }
    

    // I suspect speak is not "setup" like other functions and is not accessable like others in the
    // theml e.g. msg.speak - so got to figure that out or temporarily create a $scope.speak kludge
    $scope.speak = function(text){
        msg.send("speak", text)
        //console.log($scope.service.voice.name)
    }

    $scope.setVoice  = function(text){
        console.log($scope.service.voice.name)
        msg.send("setVoice", text.name)
        // msg.send("broadcastState")
    }

    msg.subscribe(this)
}
])