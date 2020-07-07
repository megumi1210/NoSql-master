<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>查看数据库id为1的红包信息</title>
    <script src="static/jquery-2.1.0.min.js"></script>
    <script>
        $(function () {
             $.ajax({
                 url:"redPacket/get",
                 type:"get",
                 data:{
                     redPacketId:1
                 },
                 success:function (result) {
                     $("#redPacket").html(JSON.stringify(result))
                 }
             })
        })
    </script>
</head>
<body>
       <h3>当前id=1的红包信息：</h3>
      <div id="redPacket"></div>
</body>
</html>
