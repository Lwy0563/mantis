{ nixpkgs ? <nixpkgs> }:

let pkgs = import nixpkgs {};
in {
  base = pkgs.dockerTools.buildImage {
    name = "iohk-base";
    tag = "latest";
    fromImageName = "ubuntu";
    fromImageTag = "16.04";

    runAsRoot = ''
      #!${pkgs.stdenv.shell}
      date
    '';
  };
}
