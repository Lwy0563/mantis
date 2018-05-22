{ nixpkgs
, sbtVerifySrc
, mantisSrc
}:
let pkgs = import nixpkgs {};
    sbtVerify = pkgs.callPackage ./sbt-verify.nix {
      inherit sbtVerifySrc;
    };
in rec {
  mantis = pkgs.callPackage ./mantis.nix {
    inherit mantisSrc;
    inherit sbtVerify;
  };
  mantis-docker = pkgs.dockerTools.buildImage {
    name = "iohk-base";
    tag = "latest";
    fromImageName = "ubuntu";
    fromImageTag = "16.04";

    contents = mantis;
  };
}
