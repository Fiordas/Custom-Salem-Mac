# Custom Salem Client for Mac (Apple Silicon)

This is a custom client for the Salem game, optimized to run on macOS with Apple Silicon (M1, M2, M3, etc.).

Based on [Latikai's Custom client](https://github.com/DonnEssime/Custom-Salem), which is based on [Ender's](https://github.com/EnderWiggin/Custom-Salem).

**Official Salem website:** [salemthegame.com](https://www.salemthegame.com)

## Download and Installation

### Prerequisites

You need **Java 8** installed on your Mac. Here's how to get it:

1. Download **Azul Zulu JDK 8** (which has native Apple Silicon support):

   - Visit: [https://www.azul.com/downloads/?version=java-8-lts&os=macos&architecture=arm-64-bit&package=jre#zulu](https://www.azul.com/downloads/?version=java-8-lts&os=macos&architecture=arm-64-bit&package=jre#zulu)
   - Select:
     - **Java Version:** Java 8 (LTS)
     - **Operating System:** macOS
     - **Architecture:** ARM 64-bit
     - **Java Package:** JRE
   - Download and install the `.dmg` file

2. Verify Java 8 is installed:
   ```bash
   java -version
   ```
   You should see something like `openjdk version "1.8.0_xxx"`

### Download the Client

1. Go to the [Releases page](https://github.com/Fiordas/Custom-Salem-Mac/releases)
2. Download the latest release JAR file (e.g., `salem-mac.jar`)
3. Save it to a location of your choice (e.g., your `Downloads` folder)

### Running the Client

#### Option 1: Double-click (easiest)

Simply double-click the downloaded JAR file. macOS should automatically run it with Java.

#### Option 2: Terminal (recommended)

1. Open Terminal
2. Navigate to the folder containing the JAR file:
   ```bash
   cd ~/Downloads  # or wherever you saved it
   ```
3. Run the client:
   ```bash
   java -jar salem-mac.jar
   ```

## Troubleshooting

### "The application cannot be opened"

If macOS blocks the application:

1. Go to **System Settings** → **Privacy & Security**
2. Scroll down and click **"Open Anyway"** next to the blocked application
3. Or, right-click the JAR file and select **"Open"**, then confirm

### Java version issues

If you have multiple Java versions installed, you can specify Java 8 explicitly:

```bash
/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/bin/java -jar salem-mac.jar
```

(Adjust the path based on your Java 8 installation location)

To check all installed Java versions:

```bash
/usr/libexec/java_home -V
```

## Building from Source

If you want to build the client yourself:

```bash
# Clone the repository
git clone https://github.com/Fiordas/Custom-Salem-Mac.git
cd Custom-Salem-Mac

# Build and run using ant
ant run
```

## Contributing

If you wish to contribute code or have any remarks, feel free to:

- Open an issue or pull request on GitHub
- Contact me on the Salem discord

## License

See the `COPYING` file for license information.
