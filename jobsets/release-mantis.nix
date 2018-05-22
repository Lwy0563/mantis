{ nixpkgs
, sbtVerifySrc
, mantisSrc
}:
with import nixpkgs {};
let sbtVerify = callPackage ./sbt-verify.nix {
      inherit sbtVerifySrc;
    };
in rec {
  mantis = callPackage ./mantis.nix {
    inherit mantisSrc;
    inherit sbtVerify;
  };

  docker-mantis = dockerTools.buildImage {
    name = "mantis";
    tag = "latest";
    fromImageName = "ubuntu";
    fromImageTag = "16.04";
    contents = [
      coreutils
      gnused
      gawk
      openjdk8
      mantis
    ];
    config = {
      ExposedPorts = {
        "9076/tcp" = {}; # Ethereum protocol connections
        "30303/tcp" = {}; # Discovery protocol
        "8546/tcp" = {}; # JSON-RPC http endpoint
        "5679/tcp" = {}; # Raft consensus protocol
        "8099/tcp" = {}; # Faucet.
      };
    };
  };

  exported-docker-mantis = dockerTools.exportImage {
    fromImage = docker-mantis;
    name = docker-mantis.name;
  };
}
