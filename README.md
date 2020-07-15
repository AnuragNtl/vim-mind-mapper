# vim-mind-mapper
`vim` based command line mind mapping tool
Create and manipulate complex mind maps using a simple and powerful `Groovy DSL`,
and `vim`, which helps in lesser context switches of brain, and hence lesser disturbance
### Installation
#### Docker (Preferred)
```
docker run -p 8084:8084 -p 8083:8083 -v $(pwd):/home -it anuragntl/vimmindmap:0.1.1
```
It will start `ttyd` server on `port 8084` and tasks can be edited by navigating to:
```
http://localhost:8084/
```
in the browser.

#### Direct
##### Requirements:
* python
* node js (will auto install if `snap` is present)
* groovy (will auto install if `snap` is present)
* vim (will auto install if `snap` is present)

Clone `vim-mind-mapper`:
```
$ git clone https://github.com/AnuragNtl/vim-mind-mapper.git
```

Install:
```
$ cd vim-mind-mapper
$ sudo ./install
```

### Usage:
On running task with `docker`, navigate to `http://localhost:8084`, which will present a menu.
Or on running directly, the same will be displayed.
```
$ task
```
`task file` and `options` can be passed as cmdline arguments.

Choose option `new_plain_task_file` to create a new mind map, or `read_tasks` to open existing encrypted task file.
vim will open with the editable mind map and graph spec.
By default lines are folded, which can be toggled with vim's default <kbd>Z</kbd>+<kbd>A</kbd> `[Z]`+`[A]`
![fold](images/fold.png)

`[Z]` `[A]` 
