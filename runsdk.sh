# Replace this as needed to set java runtime
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 17.0.9-zulu

# Run
mvn exec:java --define exec.mainClass="org.taxdataexchange.usage.tax1098.Tax1098DocumentGenerator"
