# SSHanyi

SSHanyi is a simple multiplatform CLI application to manage SSH config files. It is written with Kotlin Native, and utilizes the awesome [clickt](https://github.com/ajalt/clikt) library.

## Features

It currently supports:
* Adding entries to config file
* Listing entries
* Use a custom config file (apart from the default *~/.ssh/config*)

## Usage

![sshanyi-main-2](https://user-images.githubusercontent.com/16169630/227269896-7b758b2a-70f7-4b59-821a-b90ab00ab804.png)

The subcommands `list` and `add` also have their own help chapter:

![sshanyi-add](https://user-images.githubusercontent.com/16169630/227270025-d0edb691-5607-4537-b6b7-00bd4bb87b71.png)

## Installation

Simply download from the Releases section, and run without any installation. The extension of the macOS binary can be misleading, but do not be alarmed by the `.kexe` extension. You can run it as it is, or rename the executable as you wish.
