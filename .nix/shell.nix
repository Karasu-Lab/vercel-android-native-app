{
  packages ? import <nixpkgs> {
    config.allowBroken = true;
    config.allowUnfree = true;
  },
}:

packages.mkShell {
  buildInputs = with packages; [
    git
    gh
    android-tools
    kotlin-language-server
    jdk21
  ];

  pure = true;
}
