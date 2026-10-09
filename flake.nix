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
        packages = nixpkgs.legacyPackages.${system};
      in
      {
        devShells = {
          default = import ./.nix/shell.nix { inherit packages; };
          android = import ./nix/android.nix { inherit packages; };
        };
      }
    );
}
