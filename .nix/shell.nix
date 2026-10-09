{
  packages ? import <nixpkgs> {
    config.allowBroken = true;
    config.allowUnfree = true;
  },
}:

packages.mkShell {
  buildInputs = with packages; [
    git
    android-tools
    kotlin-language-server
    jdk21
    android-studio
  ];

  pure = true;
}
