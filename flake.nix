{
  description = "Vibe Android Meditation App development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            allowUnfree = true;
            android_sdk.accept_license = true;
          };
        };

        androidComposition = pkgs.androidenv.composeAndroidPackages {
          buildToolsVersions = [ "35.0.0" ];
          platformVersions = [ "35" ];
          abiVersions = [
            "armeabi-v7a"
            "arm64-v8a"
          ];
          includeEmulator = false;
          includeSources = false;
          includeSystemImages = false;
          includeNDK = false;
          useGoogleAPIs = false;
        };

        androidSdk = androidComposition.androidsdk;
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk17
            androidSdk
            gradle
          ];

          shellHook = ''
            export ANDROID_SDK_ROOT=${androidSdk}/libexec/android-sdk
            export ANDROID_HOME=$ANDROID_SDK_ROOT
            export JAVA_HOME=${pkgs.jdk17.home}
            echo "Vibe development environment loaded!"
            echo "Android SDK: $ANDROID_HOME"
            echo "Java: $(java -version 2>&1 | head -n 1)"
          '';
        };
      }
    );
}
