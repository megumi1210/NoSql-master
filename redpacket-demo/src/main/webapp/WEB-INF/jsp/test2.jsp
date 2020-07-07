<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>模拟高并发抢红包</title>
    <script src="static/jquery-2.1.0.min.js"></script>
    <script>
        $(document).ready(function () {
            //模拟30000个异步请求,进行并发
            const max = 30000;
            for(let i = 1 ; i<=max; i++){
                $.get({
                    url: "userRedPacket/grabRedPacket?redPacketId=1&userId="+i,
                    success:function (result) {

                    }

                })
            }
        })

    </script>

</head>
<body>

</body>
</html>
