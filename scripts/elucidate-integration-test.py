#!/usr/bin/env python3
import argparse

from icecream import ic

from elucidate.client import TextRepoClient


def main():
    parser = argparse.ArgumentParser(
        description="Do an integration test on the given elucidate instance.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument("elucidate_url",
                        help="The url of the elucidate server to test",
                        type=str)
    args = parser.parse_args()

    url=args.elucidate_url
    if args.elucidate_url:
        print(f"Testing {url} ...")
        tc = TextRepoClient(url)

        print(f"testing get_about...")
        result = tc.get_about()
        ic(result)
        print()

        print(f"testing read_file_types...")
        result = tc.read_file_types()
        ic(result)
        print()

        print(f"testing read_documents...")
        result = tc.read_documents()
        ic(result)
        print()


if __name__ == '__main__':
    main()
