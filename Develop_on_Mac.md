Setup MAC for Native image build
================================

The following instructions use sdkman as jvm manager. While it is not mandatory, it is recommended to ease jvm switching during development. 

1. install sdkman: `curl -s "https://get.sdkman.io" | bash` 
2. install brew: `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"`
3. manually install GraalVM (Note: check [Quarkus documentation](https://quarkus.io/guides/building-native-image#configuring-graalvm) to get current supported version): `brew install --cask graalvm/tap/(*current_graalvm_version*)`
4. link GraalVM inside sdkman: `sdk install java (*current_graalvm_version*)-grl /Library/Java/JavaVirtualMachines/(*current_graalvm_version*)/Contents/Home` 
5. go to directory containing code for native build
6. set sdkman to use GraalVM: `sdk use java (*current_graalvm_version*)-grl` 
7. issue build: `mvn clean package -Pnative`