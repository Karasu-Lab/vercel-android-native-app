{
  description = "Android App Develop";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
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
        packages = import nixpkgs {
          inherit system;
          config = {
            allowUnfree = true;
            allowBroken = true;
          };
        };
      in
      {
        devShells.default = import ./.nix/shell.nix {
          inherit packages;
        };
      }
    );
}
