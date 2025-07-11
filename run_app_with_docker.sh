source ./.env

    echo '<===== Building New Image =====>'
    echo "Building $APP_NAME"
    docker build --tag "$APP_NAME":latest .
    buildStatus=$?
    if [ $buildStatus -ne 0 ]
    then
        echo '<===== Build Failed =====>'
        echo '<===== Closing Script =====>'
        exit 1
    else
        echo '<===== Building Image Completed =====>'
        echo '<===== Creating and Running the Container =====>'
        docker run --name "$APP_NAME" --detach --publish 8080:8080 --env-file .env "$APP_NAME":latest
        echo '<===== Creating Container Completed =====>'
        echo "Running $APP_NAME in detached mode"
        docker logs --follow "$APP_NAME"
    fi

