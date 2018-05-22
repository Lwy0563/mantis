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

  docker-mantis = pkgs.dockerTools.buildImage {
    name = "mantis";
    tag = "latest";
    fromImageName = "ubuntu";
    fromImageTag = "16.04";
    contents = [
      pkgs.coreutils
      pkgs.gnused
      pkgs.gawk
      pkgs.openjdk8
      mantis
    ];
    config = {
      Cmd = [ "/bin/mantis" ];
      WorkingDir = "/";
      Volumes = {
        "/conf" = {};
      };
    };
  };
}
