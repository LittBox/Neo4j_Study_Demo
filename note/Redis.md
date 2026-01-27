## 启动 Redis 服务

方式 1：前台启动（适合调试，窗口关闭则服务停止）

1. 打开 CMD，进入当前 Redis 目录（即截图中的D:\Data\Redis-x64-3.0.504\Redis-x64-3.0.504）：

```bash
cd D:\Data\Redis-x64-3.0.504\Redis-x64-3.0.504
```

2. 执行启动命令：
```bash
redis-server.exe redis.windows.conf
```

3. 验证：CMD 窗口显示[10080] 01 Jan 16:00:00 * The server is now ready to accept connections on port 6379，表示服务启动成功。

## 连接 Redis 服务

1. 打开新的 CMD 窗口，进入 Redis 目录：
```bash
cd D:\Redis-x64-3.0.504\Redis-x64-3.0.504
```

2. 执行连接命令：
```bash 
redis-cli.exe -h 127.0.0.1 -p 6379
```

3. 验证：输入ping，返回PONG表示连接成功。