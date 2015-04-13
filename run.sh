OPT=$1
if [ $# -lt 1 ]; then
	echo "Please select a set of classes to compile.";
	exit;
fi
rm -rf build
mkdir build
if [ $OPT == "flowershop" ] ; then
	echo "Compiling flowershop..." ;
	javac -d build -cp "jar/*" -sourcepath src src/com/flowershop/flowershopsite/*.java;
	echo "Running flowershop site!";
	java -cp "build:jar/*" com.flowershop.flowershopsite.FlowerShopSite;
elif [ $OPT == "driversite" ] ; then
	echo "Compiling driversite..." ;
	javac -d build -cp "jar/*" -sourcepath src src/com/flowershop/driversite/*.java;
	echo "Running Driver site!";
	java -cp "build:jar/*" com.flowershop.driversite.DriverSite;
elif [ $OPT == "driversguild" ] ; then
	echo "Compiling drivers guild..." ;
	javac -d build -cp "jar/*" -sourcepath src src/com/flowershop/driversguild/*.java src/com/flowershop/model/*.java src/com/flowershop/driversguild/dao/*.java;
	echo "Running!";
	java -cp "build:jar/*" com.flowershop.driversguild.DriversGuild;
else
	echo "Bad option.";
	echo "Choose from: driversite, driversguild, flowershop.";
	exit;
fi
