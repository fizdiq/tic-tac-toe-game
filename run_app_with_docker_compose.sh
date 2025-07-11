source ./.env

    echo '<===== Building New Compose Containers =====>'
    docker-compose --env-file .env up -d
    buildStatus=$?
    if [ $buildStatus -ne 0 ]
    then
        echo '<===== Build Failed =====>'
        echo '<===== Closing Script =====>'
        exit 1
    else
        echo '<===== Building Compose Containers Completed =====>'
        echo "Running $APP_NAME in detached mode"
    fi