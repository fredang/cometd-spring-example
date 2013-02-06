<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<title>Cometd Spring Example</title>
	    <script type="text/javascript" src="../js/jquery-1.9.0.js"></script>
	    <script type="text/javascript" src="../js/comet/cometd-namespace.js"></script>
	    <script type="text/javascript" src="../js/comet/cometd-json.js"></script>
	    <script type="text/javascript" src="../js/comet/AckExtension.js"></script>
	    <script type="text/javascript" src="../js/comet/TransportRegistry.js"></script>
	    <script type="text/javascript" src="../js/comet/Transport.js"></script>
	    <script type="text/javascript" src="../js/comet/RequestTransport.js"></script>
	    <script type="text/javascript" src="../js/comet/WebSocketTransport.js"></script>
	    <script type="text/javascript" src="../js/comet/CallbackPollingTransport.js"></script>
	    <script type="text/javascript" src="../js/comet/LongPollingTransport.js"></script>
	    <script type="text/javascript" src="../js/comet/Utils.js"></script>
	    <script type="text/javascript" src="../js/comet/Cometd.js"></script>
	    <script type="text/javascript" src="../js/comet/jquery.cometd.js"></script>
	</head>
	<body>
<script type="text/javascript">

function startTwitterService() {
	$('#twitterStreamStatus').text("Starting...");
	$.post('startTwitterService', {
		"username": $('INPUT[name=username]').val(),
		"password": $('INPUT[name=password]').val()
	}, function() {
		$('#twitterStreamStatus').text("Started");
	});
	return false;
}

function stopTwitterService() {
	$('#twitterStreamStatus').text("Stopping...");
	$.post('stopTwitterService', null, function() {
		$('#twitterStreamStatus').text("Stopped");
	});
	return false;
}

function startSubscription() {
	cometd.subscribe('/twitter/samples', function(msg) {
		if (msg.data.status == 'ERR') {
			alert("Error: " + msg.data.text);
			$('#twitterStreamStatus').text("Error");
		} else {
			$('#tweetTable>tbody').prepend($('<tr>'
				+ '<td><img src="' + msg.data.profileImageUrl + '" /></td>'
				+ '<td>' + msg.data.createdAt + '</td>'
				+ '<td>' + msg.data.username + '</td>'
				+ '<td>' + msg.data.text + '</td>'
				+ '</tr>'));
			$('#tweetTable>tbody>tr:eq(15)').remove();
			tweetCount++;
			$('#tweetCount').text("" + tweetCount);
		}
	});
}
		
var cometd = $.cometd;
cometd.registerTransport('long-polling', new org.cometd.LongPollingTransport());
cometd.registerTransport('callback-polling', new org.cometd.CallbackPollingTransport());

var r = document.location.href.match(/^(.*)\/spring/);
cometd.init(r[1] + '/cometd');

var tweetCount = 0;

cometd.addListener('/meta/handshake', function(handshake) {
	if (handshake.successful === true ) {
		cometd.batch(function () {
			startSubscription();
		});
	}
});

cometd.handshake();

</script>	
		<h1>CometD Spring Demo</h1>
		Twitter username:
		<input type="text" name="username" />
		password:
		<input type="password" name="password" />
		<input type="button" value="Start" onClick="startTwitterService()" />
		<input type="button" value="Stop" onClick="stopTwitterService()" />
		<br /><br />
		Twitter Stream Status: <span id="twitterStreamStatus">-</span>
		, Tweets received: <span id="tweetCount">-</span>
		<br /><br />
		<table id="tweetTable" border="1">
			<colgroup>
				<col width="20" />
				<col width="220" />
				<col width="200" />
				<col width="600" />
			</colgroup>
			<thead>
				<th>Img</th>
				<th>Created</th>
				<th>Username</th>
				<th>Status</th>
			</thead>
			<tbody>
			</tbody>
		</table>
	</body>
</html>
