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
    name = "mantis";
    tag = "latest";
    contents = mantis;
    fromImage = pkgs.dockerTools.buildImage {
      name = "iohk-java";
      tag = "latest";
      contents = pkgs.openjdk8;
      fromImage = {
        name = "iohk-base";
        tag = "latest";
        contents = pkgs.stdenv;
        fromImageName = "ubuntu";
        fromImageTag = "16.04";
      };
    };
  };
}
