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

  base = pkgs.dockerTools.buildImage {
    name = "iohk-base";
    tag = "latest";
    fromImageName = "ubuntu";
    fromImageTag = "16.04";
    contents = pkgs.coreutils;
  };
  withJdk = pkgs.dockerTools.buildImage {
    name = "iohk-java";
    tag = "latest";
    fromImage = base;
    contents = pkgs.openjdk8;
  };
  mantis-docker = pkgs.dockerTools.buildImage {
    name = "mantis";
    tag = "latest";
    fromImage = withJdk;
    contents = mantis;
  };
}
