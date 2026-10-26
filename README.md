![Upgraded with Claude](https://img.shields.io/badge/Upgraded%20with-Claude-D97757)

# NettyVSGPRC

A small benchmark that compares the round-trip latency of two ways to move a message between a client and a server:

* a plain **HTTP/1.1 echo server built on [Netty](https://netty.io/)**, and
* a **[gRPC](https://grpc.io/) echo server** (protobuf over HTTP/2).

Both servers answer every request with the exact message they received. The client sends batches of random messages
of different sizes (100 characters up to 100,000 characters, plus the text of the bible) and prints minimum, maximum,
average, 90th and 99th percentile times in milliseconds.

## Requirements

* JDK 21 or newer
* Maven 3.9+

## Build and test

```bash
mvn verify
```

This generates the gRPC stubs from `src/main/proto/simplemessage.proto`, runs the tests and builds a runnable jar
in `target/test-webservers-2.0.0.jar`.

## Run

```bash
# start both servers and benchmark them on this machine (default)
java -jar target/test-webservers-2.0.0.jar

# only start the servers (Netty on 9999, gRPC on 9998)
java -jar target/test-webservers-2.0.0.jar server

# only run the benchmark against servers running on another machine
java -jar target/test-webservers-2.0.0.jar client 192.0.2.10
```

The "send the bible" test reads `bible.txt` from the current directory and is skipped if it is missing.

## Upgraded after 9 years

This project was originally written in 2017 and was upgraded in 2026 with Claude. What changed:

| Area | Before (2017) | Now |
|------|---------------|-----|
| Language | Java 8 | Java 21 (records, text blocks, switch expressions, `java.net.http`) |
| gRPC | 1.3.0 | 1.85.1 (`grpc-netty-shaded`) |
| protobuf | 3.2.0 | 4.36.2 |
| Netty | 4.1.11 | 4.2.19 (new `MultiThreadIoEventLoopGroup` API) |
| Tests | JUnit 3 with `assertTrue(true)` | JUnit 5: unit tests for the statistics and real end-to-end echo tests for both servers |
| Build plugins | shade + assembly + exec, old versions | current shade, surefire and protobuf plugins; assembly and exec removed |

Code changes:

* **Netty server**: uses `HttpServerCodec` + `HttpObjectAggregator`. The old handler lost data when a request arrived
  in several chunks and appended `\r\n` to the echo; it now echoes the body byte for byte.
* **Netty client**: `HttpURLConnection` replaced by `java.net.http.HttpClient`.
* **gRPC**: the deprecated `usePlaintext(true)` is now `usePlaintext()`.
* **Bug fix**: oversized messages were "truncated" with `substring(4194000)`, which kept only the tail
  (and nothing for short strings). They are now cut to the first 4,000,000 characters so they fit the 4MB limits.
* **Servers and clients are `AutoCloseable`** and can bind to port 0, which is what makes the tests possible.
* **Benchmark**: timings use `System.nanoTime`, statistics live in an immutable `Stats` record, and an empty run
  no longer divides by zero. `commons-lang` and Guava are no longer needed.
* **CLI**: `both`, `server`, `client <host>` instead of the old ad-hoc argument handling. The `netty.port` /
  `grpc.port` arguments were removed; ports are fixed at 9999 and 9998.
* **Removed**: the committed 10MB `test-webserver.jar` build artifact and the Heroku `Procfile`, which hard-coded an IP
  address. Build the jar yourself with `mvn verify`.

## License

[Apache License 2.0](LICENSE), Copyright 2017-2026 Andrea Lorenzani. The original Netty-derived handlers were
rewritten; dependencies keep their own licenses (Apache 2.0 for Netty, gRPC; BSD-3-Clause for protobuf).
